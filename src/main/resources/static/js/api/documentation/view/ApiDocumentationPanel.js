let API_DOCUMENTATION_VARIABLES = {}

Ext.Ajax.request({
    method: 'GET',
    url: '/api/10/getFiat',
    async: false,
    success: function (response) {
        API_DOCUMENTATION_VARIABLES.fiats = response.responseText
    }
})

Ext.define('ApiDocumentation.view.ApiDocumentationPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'apidocumentationpanel',
    requires: [
        'ApiDocumentation.view.CreateDealFieldSet',
        'ApiDocumentation.view.PaidFieldSet',
        'ApiDocumentation.view.CancelDealFieldSet',
        'ApiDocumentation.view.GetStatusFieldSet'
    ],

    title: 'API документация',
    maxWidth: 800,
    width: '100%',
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        margin: '0 10 10 10'
    },
    items: [
        {
            xtype: 'createdealfieldset'
        },
        {
            xtype: 'paidfieldset'
        },
        {
            xtype: 'canceldealfieldset'
        },
        {
            xtype: 'getstatusfieldset'
        }
    ]
})