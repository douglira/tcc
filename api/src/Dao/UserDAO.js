const ConnectionFactory = require('../Models/Factories/ConnectionFactory')

class UserDAO {
  constructor () {
    this.conn = ConnectionFactory.getConnection()

    if (!this.conn || this.conn === undefined) {
      throw new Error('Não foi possível iniciar uma conexão')
    }
  }

  static create (user) {}
}

module.exports = UserDAO
