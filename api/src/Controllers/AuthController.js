const moment = require('moment')

const User = require('../Models/User')
const Person = require('../Models/Person')
const Address = require('../Models/Address')
const Token = require('../Models/Token')

const UserDAO = require('../Dao/UserDAO')

const { MessageQueue } = require('../Services/MessageQueue')

module.exports = {
  async signup (req, res, next) {
    try {
      const data = req.body

      if (
        !data.user.email ||
        !data.user.password ||
        !data.user.confirm_password
      ) {
        return res.status(400).json({ error: 'Campos inválidos' })
      }

      if (data.user.password !== data.user.confirmPassword) {
        return res.status(400).json({ error: 'Senhas não coincidem' })
      }

      const address = new Address()
      const person = new Person()
      const user = new User()

      address.zipCode = data.address.zipCode
      address.street = data.address.street
      address.district = data.address.district
      address.city = data.address.city
      address.provinceCode = data.address.provinceCode
      address.countryName = data.address.countryName
      address.buildingNumber = data.address.buildingNumber
      address.additionalData = data.address.additionalData

      person.name = data.person.name
      person.birthday = moment(data.person.birthday, 'DD/MM/YYYY').toDate()
      person.tel = data.person.tel
      person.cel = data.person.cel
      person.rg = data.person.rg
      person.cpf = data.person.cpf

      user.email = data.user.email
      user.password = data.user.password
      user.role = 'user'

      person.address = address
      user.person = person

      user.setDisplayName()
      await user.hashPassword(user.password)
      await new UserDAO().create(user)

      return res.status(201).json()
    } catch (err) {
      return next(err)
    }
  },

  async signin (req, res, next) {
    try {
      const { email, password } = req.body

      if (!email || !password) {
        return res.status(400).send({ error: 'Campos inválidos' })
      }

      const userData = new User()
      userData.email = email
      userData.password = password
      const user = await new UserDAO().checkIfExists(userData)

      if (!user) {
        return res.status(400).send({ error: 'Email ou senha inválidos' })
      }

      if (user.status !== 'active') {
        return res.status(401).send({ error: 'Conta desativada' })
      }

      if (!(await user.verifyPassword(userData.password))) {
        return res.status(400).send({ error: 'Email ou senha inválidos' })
      }

      const token = await Token.generate(user)

      delete user.password

      return res.json({ token, user })
    } catch (err) {
      return next(err)
    }
  },

  async forgotPass (req, res, next) {
    try {
      const { email } = req.body

      if (!email) {
        return res.status(400).send({ error: 'Campo inválido' })
      }

      const userData = new User()
      userData.email = email
      const user = await new UserDAO().checkIfExists(userData)

      if (!user) {
        return res.status(400).send({ error: 'Campo inválido' })
      }

      user.resetPassword()
      await new UserDAO().resetPassword(user)

      const mq = new MessageQueue()
      mq.sendMailForgotPass({
        from: 'SmartSearch <noreply@smartsearch.com>',
        to: user.email,
        subject: 'SmartSearch - Redefinição de senha',
        template: 'forgotPass',
        context: {
          displayName: user.displayName,
          token: user.passwordResetToken
        }
      })

      return res.json({
        message:
          'Solicitação efetuada com sucesso. Em breve receberá um email para redefinir de senha'
      })
    } catch (err) {
      return next(err)
    }
  },

  async resetPass (req, res, next) {
    try {
      const { token, password, confirmPassword } = req.body

      if (!token) {
        return res
          .status(400)
          .json({ error: 'Token não fornecido. Verifique seu email' })
      }

      if (!password || !confirmPassword) {
        return res.status(400).json({ error: 'Campos inválidos' })
      }

      if (password !== confirmPassword) {
        return res.status(400).json({ error: 'Senhas não coincidem' })
      }

      let user = new User()
      user.passwordResetToken = token
      user = await new UserDAO().findByPasswordToken(user)

      if (!user) {
        return res.status(400).json({ error: 'Token inválido' })
      }

      if (!user.isExpiredResetPassword()) {
        return res.status(400).json({
          error: 'Redefinição de senha expirada. Por favor, solicite novamente'
        })
      }

      await user.hashPassword(password)
      await new UserDAO().updateResetPassword(user)

      return res.json({ message: 'Senha redefinida com sucesso' })
    } catch (err) {
      return next(err)
    }
  }
}
