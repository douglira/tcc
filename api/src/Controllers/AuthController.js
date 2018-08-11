const moment = require('moment')

const User = require('../Models/User')
const Person = require('../Models/Person')
const Address = require('../Models/Address')

const UserDAO = require('../Dao/UserDAO')

module.exports = {
  async register (req, res, next) {
    try {
      const data = req.body

      if (
        !data.user.email ||
        !data.user.password ||
        !data.user.confirm_password
      ) {
        return res.status(400).json({ error: 'Campos inválidos' })
      }

      if (data.user.password !== data.user.confirm_password) {
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
  }
}
