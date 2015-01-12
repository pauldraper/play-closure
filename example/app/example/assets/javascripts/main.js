goog.require('goog.dom');
goog.require('example.message');

var messageDom = goog.dom.htmlToDocumentFragment(example.message);
document.body.appendChild(messageDom);
