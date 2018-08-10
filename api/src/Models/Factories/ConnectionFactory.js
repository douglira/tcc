const config = require('../../../config/database')

class ConnectionFactory {
  constructor () {
    this.db = require('knex')({
      client: config.client,
      connection: config[config.client],
      pool: { min: 0, max: 10 }
    })
  }

  static getConnection () {
    return this.db
  }
}

module.exports = ConnectionFactory
