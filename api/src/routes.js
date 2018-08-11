const express = require('express')
const requireDir = require('require-dir')

const routes = express.Router()

const controllers = requireDir('./Controllers')
const middlewares = requireDir('./Middlewares')

/**
 * Auth
 */
routes.post('/auth/register', controllers.AuthController.register)

routes.use(middlewares.Authorization)

/**
 * Error
 */
routes.use(middlewares.Error)

module.exports = routes
