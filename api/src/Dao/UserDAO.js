const ConnectionFactory = require('../Models/Factories/ConnectionFactory')

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
}

module.exports = UserDAO
