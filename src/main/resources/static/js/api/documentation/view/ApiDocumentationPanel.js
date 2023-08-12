Ext.define('ApiDocumentation.view.ApiDocumentationPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'apidocumentationpanel',
    title: 'API документация',
    maxWidth: 1000,
    width: '100%',
    layout: {
        type: 'accordion'
    },
    items: [
        {
            title: 'Создание сделки',
            html: '<b>URL:</b> /api/10/new'
            + '<b>Метод:</b> POST'
            + '<b>Параметры:</b>'
        },
        {
            title: 'Подтверждение оплаты'
        },
        {
            title: 'Отмена сделки'
        },
        {
            title: 'Проверка статуса'
        }
    ]
})