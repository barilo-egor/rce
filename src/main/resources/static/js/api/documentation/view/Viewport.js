Ext.define('ApiDocumentation.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'ApiDocumentation.view.ApiDocumentationPanel'
    ],
    alias: 'widget.apiDocumentationViewport',
    layout: {
        type: 'vbox',
        align: 'center'
    },
    viewModel: true,
    items: [
        {
            flex: 1,
            xtype: 'apidocumentationpanel'
        }
    ]
});