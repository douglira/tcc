const ConnectionFactory = require('../Models/Factories/ConnectionFactory')

const User = require('../Models/User')
const Person = require('../Models/Person')

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

  async index (page, perPage) {
    const offset = (page - 1) * perPage

    const [result, { count: total }] = await Promise.all([
      this.conn('users')
        .select(
          'users.id as user_id',
          'users.avatar as user_avatar',
          'users.email as user_email',
          'users.displayName as user_displayName',
          'users.status as user_status',
          'people.id as people_id',
          'people.name as people_name',
          'people.birthday as people_birthday',
          'people.tel as people_tel',
          'people.cel as people_cel',
          'people.rg as people_rg',
          'people.cpf as people_cpf'
        )
        .innerJoin('people', 'users.id', 'people.userId')
        .orderBy('people_name', 'asc')
        .offset(offset)
        .limit(perPage),
      this.conn
        .count('*')
        .from('users')
        .innerJoin('people', 'users.id', 'people.userId')
        .first()
    ])

    const users = result.map(row => {
      const user = new User()
      const person = new Person()

      user.id = row.user_id
      user.avatar = row.user_avatar
      user.email = row.user_email
      user.displayName = row.user_displayName
      user.status = row.user_status
      person.id = row.people_id
      person.name = row.people_name
      person.birthday = row.people_birthday
      person.tel = row.people_tel
      person.cel = row.people_cel
      person.rg = row.people_rg
      person.cpf = row.people_cpf
      user.person = person

      return user
    })

    return {
      total: parseInt(total, 10),
      page: parseInt(page, 10),
      perPage: parseInt(perPage, 10),
      lastPage: Math.ceil(total / perPage),
      data: users
    }
  }

  async toggleStatus (user, changedBy) {
    const result = await this.conn('users')
      .select('status')
      .where('id', user.id)
      .first()

    if (!result) {
      throw new Error('Usuário não existe')
    }

    if (result.status === 'active') {
      await this.conn('users')
        .where('id', user.id)
        .update({
          updatedAt: new Date(),
          status: 'inactive',
          lastActive: new Date(),
          statusChangedBy: changedBy.id
        })
    } else {
      await this.conn('users')
        .where('id', user.id)
        .update({
          updatedAt: new Date(),
          status: 'active',
          lastInactive: new Date(),
          statusChangedBy: changedBy.id
        })
    }
  }
}

module.exports = UserDAO
