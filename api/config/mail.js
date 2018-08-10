module.exports = {
  connection: process.env.MAIL_CONNECTION || 'smtp',

  smtp: {
    host: process.env.SMTP_HOST,
    port: process.env.SMTP_PORT,
    user: process.env.SMTP_USERNAME,
    pass: process.env.SMTP_PASSWORD
  }
}
