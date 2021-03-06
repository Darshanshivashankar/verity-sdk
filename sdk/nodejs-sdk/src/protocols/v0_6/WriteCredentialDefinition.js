'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class WriteCredentialDefinition extends Protocol {
  constructor (name, schemaId, tag = null, revocation = null, threadId = null) {
    const msgFamily = 'write-cred-def'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.name = name
    this.schemaId = schemaId
    this.tag = tag
    this.revocation = revocation

    this.msgNames.WRITE_CRED_DEF = 'write'
  }

  async writeMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.WRITE_CRED_DEF)
    msg.name = this.name
    msg.schemaId = this.schemaId
    msg.tag = this.tag
    msg.revocationDetails = this.revocation
    msg = this._addThread(msg)
    return msg
  }

  async writeMsgPacked (context) {
    return this.getMessageBytes(context, await this.writeMsg(context))
  }

  async write (context) {
    await this.sendMessage(context, await this.writeMsgPacked(context))
  }
}
