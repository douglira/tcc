class UserDAO {
  constructor (conn) {
    if (!conn) {
      throw new Error('Missing connection parameter')
    }

    this.conn = conn
  }
}

module.exports = UserDAO
