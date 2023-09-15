Ext.define('ApiDocumentation.view.GetStatusFieldSet', {
    extend: 'Ext.form.FieldSet',
    xtype: 'getstatusfieldset',

    title: 'Получение статуса сделки',
    collapsible: true,
    collapsed: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        margin: '0 0 10 0'
    },
    items: [
        {
            xtype: 'component',
            html: 'Для получения статуса сделки необходимо отправить <b>GET</b> запрос.',
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    flex: 1,
                    xtype: 'textfield',
                    listeners: {
                        afterrender: function (me) {
                            ExtUtil.request({
                                url: '/api/10/getUrl',
                                method: 'GET',
                                success: function (response) {
                                    me.setValue(response.data + '/api/10/getStatus')
                                }
                            })
                        }
                    },
                    editable: false,
                    fieldLabel: 'URL',
                    labelWidth: 30
                },
                {
                    xtype: 'button',
                    width: 120,
                    text: 'Скопировать',
                    handler: function (btn) {
                        navigator.clipboard.writeText(btn.up('container').items.items[0].getValue())
                        Ext.toast('Ссылка скопирована в буфер обмена.')
                    }
                }
            ]
        },
        {
            xtype: 'grid',
            title: 'Параметры',
            collapsible: true,
            store: Ext.create('Ext.data.Store', {
                fields: [
                    'name', 'type', 'description'
                ],
                data: [
                    {
                        name: 'id',
                        type: 'Number',
                        description: 'Идентификатор заявки'
                    },
                    {
                        name: 'token',
                        type: 'String',
                        description: 'Ваш api-токен'
                    }
                ]
            }),
            columns: [
                {
                    width: 150,
                    text: 'Параметр',
                    dataIndex: 'name'
                },
                {
                    width: 100,
                    text: 'Тип',
                    dataIndex: 'type'
                },
                {
                    flex: 1,
                    text: 'Описание',
                    dataIndex: 'description'
                }
            ]
        },
        {
            xtype: 'panel',
            title: 'Пример ответа',
            collapsible: true,
            collapsed: true,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'textarea',
                    padding: '10 0 0 0',
                    height: 155,
                    width: 400,
                    editable: false,
                    value: '{\n' +
                        '   "code": 8,\n' +
                        '   "description": "Сделка найдена.",\n' +
                        '   "data":{\n' +
                        '      "status": "PAID"\n' +
                        '   }\n' +
                        '}'
                }
            ]
        },
        {
            xtype: 'panel',
            title: 'Пример ответа в случае ошибки',
            collapsible: true,
            collapsed: true,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'textarea',
                    padding: '10 0 0 0',
                    height: 95,
                    width: 400,
                    editable: false,
                    value: '{\n' +
                        '   "code": 11,\n' +
                        '   "description": "Сделка не найдена."\n' +
                        '}'
                }
            ]
        },
        {
            xtype: 'grid',
            title: 'Коды ответа',
            collapsible: true,
            collapsed: true,
            store: Ext.create('Ext.data.Store', {
                fields: ['code', 'description'],
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url: '/api/10/statusCodes/getStatuses',
                    reader: {
                        type: 'json',
                        rootProperty: 'body.data'
                    }
                }
            }),
            columns: [
                {
                    width: 50,
                    text: 'Код',
                    dataIndex: 'code'
                },
                {
                    flex: 1,
                    text: 'Описание',
                    dataIndex: 'description'
                },
                {
                    width: 35,
                    dataIndex: 'code',
                    renderer: function (val) {
                        if (val !== 8) {
                            return '<i class="fas fa-circle redColor"></i>'
                        } else {
                            return '<i class="fas fa-circle limeColor"></i>'
                        }
                    }
                }
            ]
        },
        {
            xtype: 'grid',
            title: 'Статусы сделки',
            collapsible: true,
            collapsed: true,
            store: Ext.create('Ext.data.Store', {
                fields: ['name', 'description'],
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url: '/api/10/getDealStatuses',
                    reader: {
                        type: 'json',
                        rootProperty: 'body.data'
                    }
                }
            }),
            columns: [
                {
                    width: 100,
                    text: 'Статус',
                    dataIndex: 'name'
                },
                {
                    flex: 1,
                    text: 'Описание',
                    dataIndex: 'description'
                }
            ]
        },
    ]
})