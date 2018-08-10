const Image = require('./Image')

class User {
  constructor (username, password) {
    this.id = null
    this.avatar = new Image()
    this.username = username || null
    this.email = null
    this.password = password || null
    this.passwordResetToken = null
    this.passwordExpiresIn = null
    this.role = null
    this.status = null
    this.lastActive = null
    this.lastInactive = null
    this.createdAt = null
    this.updatedAt = null
  }
}

module.exports = User
