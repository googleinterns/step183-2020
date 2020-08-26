let convert = require('../go.js');
var assert = require('assert');

describe('standardizeTime()', function() {
  it('should return 00 when the input is 0', function(){
    assert.equal('00', convert.standardizeTime(0));
  });
});