const config = require('../../../knexfile')

class ConnectionFactory {
  constructor () {
    this.db = require('knex')(config)
  }

  static getConnection () {
    return this.db
  }
}

module.exports = ConnectionFactory
