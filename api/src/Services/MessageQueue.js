const kue = require('kue')
const config = require('../../config/redis')

class MessageQueue {
  constructor () {
    this.queue = kue.createQueue(config)

    this.queue.on('ready', () => console.log('Queue is ready'))
    this.queue.on('error', err => console.log('Queue error: ', err))
  }

  sendMailForgotPass (data, done) {
    return this.queue
      .create('mailer:forgotPass', data)
      .attempts(3)
      .removeOnComplete(true)
      .save(err => {
        if (err) {
          console.error('MQ.sendMailForgotPass', err)
          done(err)
        }
        if (!err) {
          done()
        }
      })
  }
}

module.exports.MessageQueue = MessageQueue
