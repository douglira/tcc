exports.up = function (knex) {
  return knex.schema.createTable('people', table => {
    table.increments()
    table.string('name').notNullable()
    table.date('birthday').notNullable()
    table.bigInteger('tel', 15).notNullable()
    table.bigInteger('cel', 15).nullable()
    table
      .bigInteger('rg', 11)
      .notNullable()
      .unique()
    table
      .bigInteger('cpf', 11)
      .notNullable()
      .unique()

    table
      .integer('addressId')
      .notNullable()
      .unsigned()
      .references('id')
      .inTable('addresses')
      .onUpdate('CASCADE')
      .onDelete('CASCADE')

    table
      .integer('userId')
      .notNullable()
      .unsigned()
      .references('id')
      .inTable('users')
      .onUpdate('CASCADE')
      .onDelete('CASCADE')

    table.timestamp('createdAt', true)
    table.timestamp('updatedAt', true)
  })
}

exports.down = function (knex) {
  return knex.schema.dropTable('people')
}
