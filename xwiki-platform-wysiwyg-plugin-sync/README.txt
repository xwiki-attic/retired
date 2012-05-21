This is an old experimental real-time synchronization plugin for the WYSIWYG content editor. If you're looking for real-time rich text editing you should check the Wiki 3.0 ( https://github.com/xwiki-contrib/wiki30 ) project instead. Otherwise, you can try this plugin by following this steps:

* take the code and build it
* add the client and server side as dependencies to the WYSIWYG client and server side respectively
* modify WysiwygEditorFactory to register the factory of the sync plugin
* rebuild the editor and update the jars and client side resources of your XWiki Enterprise instance
* configure the WYSIWYG content editor to load the sync plugin