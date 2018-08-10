const File = require('File')

class Image extends File {
  constructor (url) {
    super()
    this.url = url || null
  }
}

module.exports = Image
