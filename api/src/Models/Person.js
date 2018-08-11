const Address = require('./Address')

module.exports = class Person {
  constructor () {
    this.id = undefined
    this.name = undefined
    this.birthday = undefined
    this.tel = undefined
    this.cel = undefined
    this.rg = undefined
    this.cpf = undefined
    this.createdAt = undefined
    this.updatedAt = undefined
    this.address = new Address()
  }
}
