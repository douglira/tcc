const ConnectionFactory = require('../Models/Factories/ConnectionFactory')

const User = require('../Models/User')

class UserDAO {
  constructor () {
    this.conn = ConnectionFactory.getConnection()

    if (!this.conn || this.conn === undefined) {
      throw new Error('Não foi possível iniciar uma conexão')
    }
  }

  async create (user) {
    await this.conn.transaction(async trx => {
      const [userId] = await trx
        .insert(
          {
            displayName: user.displayName,
            email: user.email,
            password: user.password,
            role: user.role,
            createdAt: new Date(),
            updatedAt: new Date()
          },
          'id'
        )
        .into('users')

      const [addressId] = await trx
        .insert(
          {
            zipCode: user.person.address.zipCode,
            street: user.person.address.street,
            district: user.person.address.district,
            city: user.person.address.city,
            provinceCode: user.person.address.provinceCode,
            countryName: user.person.address.countryName,
            buildingNumber: user.person.address.buildingNumber,
            additionalData: user.person.address.additionalData,
            createdAt: new Date(),
            updatedAt: new Date()
          },
          'id'
        )
        .into('addresses')

      delete user.person.address
      await trx
        .insert(
          {
            name: user.person.name,
            birthday: user.person.birthday,
            tel: user.person.tel,
            cel: user.person.cel,
            rg: user.person.rg,
            cpf: user.person.cpf,
            createdAt: new Date(),
            updatedAt: new Date(),
            addressId,
            userId
          },
          'id'
        )
        .into('people')
    })
  }

  async checkIfExists (user) {
    const result = await this.conn
      .select('*')
      .from('users')
      .where('email', user.email)
      .first()

    if (!result) {
      return undefined
    }

    const userData = new User()
    userData.id = result.id
    userData.avatar = result.avatar
    userData.displayName = result.displayName
    userData.email = result.email
    userData.password = result.password
    userData.role = result.role
    userData.createdAt = result.createdAt
    userData.updatedAt = result.updatedAt
    userData.status = result.status
    userData.lastActive = result.lastActive
    userData.lastInactive = result.lastInactive
    userData.statusChangedBy = result.statusChangedBy

    return userData
  }

  async resetPassword (user) {
    await this.conn('users')
      .where('id', user.id)
      .update({
        passwordResetToken: user.passwordResetToken,
        passwordExpiresIn: user.passwordExpiresIn
      })
  }

  async findByPasswordToken (user) {
    const result = await this.conn
      .select(['id', 'email', 'passwordResetToken', 'passwordExpiresIn'])
      .from('users')
      .where('passwordResetToken', user.passwordResetToken)
      .first()

    if (!result) {
      return undefined
    }

    const userData = new User()
    userData.id = result.id
    userData.email = result.email
    userData.passwordResetToken = result.passwordResetToken
    userData.passwordExpiresIn = result.passwordExpiresIn

    return userData
  }

  async updateResetPassword (user) {
    return this.conn('users')
      .where('id', user.id)
      .update({
        password: user.password,
        passwordResetToken: null,
        passwordExpiresIn: null
      })
  }
}

module.exports = UserDAO
