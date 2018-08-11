const bcrypt = require('bcryptjs')

const Person = require('./Person')
const Image = require('./Image')

module.exports = class User {
  constructor (email = undefined, password = undefined) {
    this.id = undefined
    this.displayName = undefined
    this.email = email
    this.password = password
    this.passwordResetToken = undefined
    this.passwordExpiresIn = undefined
    this.role = undefined
    this.status = undefined
    this.lastActive = undefined
    this.lastInactive = undefined
    this.createdAt = undefined
    this.updatedAt = undefined
    this.person = new Person()
    this.avatar = new Image()
  }

  setDisplayName () {
    if (!this.person || !this.person.name) {
      throw new Error('You have to set a person name')
    }

    const fullName = this.person.name

    const splittedName = fullName.split(' ')

    let [first] = splittedName.slice(0, 1)
    let [middle] = splittedName.slice(1, 2)
    let [last] = splittedName.slice(-1)

    let displayName

    if (splittedName.length === 2) {
      displayName = `${first} ${last}`
      return displayName
    }

    if (first.length <= 3) {
      first = `${first} ${middle}`
    }

    displayName = `${first} ${last}`
    this.displayName = displayName
  }

  async hashPassword (password) {
    this.password = await bcrypt.hash(password, 10)
  }
}
