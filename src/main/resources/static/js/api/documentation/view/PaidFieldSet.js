Ext.define('ApiDocumentation.view.PaidFieldSet', {
    extend: 'Ext.form.FieldSet',
    xtype: 'paidfieldset',

    title: 'Подтверждение оплаты',
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
            html: 'Для подтверждения оплаты сделки необходимо отправить <b>POST</b> запрос.',
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
                                    me.setValue(response.data + '/api/10/paid')
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
            title: 'Пример ответа в случае, если оплата подтверждена',
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
                        '   "code": 9,\n' +
                        '   "description": "Сделка переведена в статус "Оплачено"."\n' +
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
                        '   "code": 13,\n' +
                        '   "description": "Сделка уже в статусе "Оплачено"."\n' +
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
                    url: '/api/10/statusCodes/paid',
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
                        if (val !== 9) {
                            return '<i class="fas fa-circle redColor"></i>'
                        } else {
                            return '<i class="fas fa-circle limeColor"></i>'
                        }
                    }
                }
            ]
        },
    ]
})