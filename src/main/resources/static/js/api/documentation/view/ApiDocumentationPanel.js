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
    title: 'API документация',
    maxWidth: 800,
    width: '100%',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        xtype: 'fieldset',
        margin: '0 10 10 10'
    },
    items: [
        {
            title: 'Создание сделки',
            collapsible: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                padding: '0 0 10 0'
            },
            items: [
                {
                    xtype: 'component',
                    html: 'Для создания сделки необходимо отправить POST запрос.<br>',
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
                                            me.setValue(response.body.data + '/new')
                                        }
                                    })
                                }
                            },
                            value: 'http://localhost:8080/api/10/documentation',
                            editable: false,
                            fieldLabel: 'URL',
                            labelWidth: 30
                        },
                        {
                            xtype: 'button',
                            width: 120,
                            text: 'Скопировать',
                            handler: function () {
                                navigator.clipboard.writeText('http://localhost:8080/api/10/documentation')
                            }
                        }
                    ]
                },
                {
                    xtype: 'grid',
                    title: 'Параметры',
                    store: Ext.create('Ext.data.Store', {
                        fields: [
                            'name', 'type', 'description'
                        ],
                        data: [
                            {
                                name: 'token',
                                type: 'String',
                                description: 'Ваш api-токен'
                            },
                            {
                                name: 'dealType',
                                type: 'String',
                                description: 'Тип сделки: BUY - покупка, SELL - продажа'
                            },
                            {
                                name: 'fiatCurrency',
                                type: 'String',
                                description:  API_DOCUMENTATION_VARIABLES.fiats
                            },
                            {
                                name: 'cryptoCurrency',
                                type: 'String',
                                description: 'Криптовалюта: BITCOIN, LITECOIN, USDT, MONERO'
                            },
                            {
                                name: 'amount',
                                type: 'Decimal',
                                description: 'Сумма в фиате'
                            },
                            {
                                name: 'cryptoAmount',
                                type: 'Decimal',
                                description: 'Сумма к криптовалюте'
                            },
                            {
                                name: 'requisite',
                                type: 'String',
                                description: 'Ваши реквизиты.'
                            },
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
                }
            ]
        }
    ]
})