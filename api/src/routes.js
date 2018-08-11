const express = require('express')
const requireDir = require('require-dir')

const Router = express.Router()

const controllers = requireDir('./Controllers')
const middlewares = requireDir('./Middlewares')

/**
 * Auth
 */
Router.post('/auth/signup', controllers.AuthController.signup)
Router.post('/auth/signin', controllers.AuthController.signin)
Router.post('/auth/forgot_password', controllers.AuthController.forgotPass)
Router.post('/auth/reset_password', controllers.AuthController.resetPass)

Router.use(middlewares.Authorization)

/**
 * Error
 */
Router.use(middlewares.Error)

module.exports = Router
