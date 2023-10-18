Ext.define('Main.view.paymentTypes.PaymentTypesPanel', {
    extend: 'Main.view.components.FramePanel',
    xtype: 'paymenttypespanel',
    title: {
        xtype: 'mainframetitle',
        text: 'Типы оплат'
    },
    requires: ['Main.view.paymentTypes.PaymentTypeWindow'],

    padding: '0 0 0 20',
    dockedItems: [
        {
            xtype: 'toolbar',
            defaults: {
                xtype: 'button'
            },
            items: [
                {
                    iconCls: 'fas fa-plus',
                    tooltip: 'Создать тип оплаты',
                    handler: function (me) {
                        Ext.create('Main.view.paymentTypes.PaymentTypeWindow', {
                            viewModel: {
                                data: {
                                    title: 'Создание типа оплаты'
                                }
                            }
                        })
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'grid',
            emptyText: 'Нет записей',
            store: {
                storeId: 'paymentTypesStore',
                fields: ['pid', 'title', 'isOn'],
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url: '/web/paymentTypes/findAll',
                    reader: {
                        type: 'json',
                        rootProperty: 'body.data'
                    }
                }
            },
            columns: [
                {
                    flex: 1,
                    dataIndex: 'name',
                    text: 'Название'
                },
                {
                    width: 35,
                    text: '<i class="fas fa-power-off"></i>',
                    dataIndex: 'isOn',
                    renderer: function (val) {
                        if (val) {
                            return '<i class="fas fa-circle limeColor"></i>'
                        } else {
                            return '<i class="fas fa-circle redColor"></i>'
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    width: 35,
                    handler: function (view, rowIndex, collIndex, item, e, record) {
                        ExtUtil.request({
                            url: '/web/paymentTypes/get',
                            method: 'GET',
                            params: {
                                pid: record.get('pid')
                            },
                            success: function (response) {
                                Ext.create('Main.view.paymentTypes.PaymentTypeWindow', {
                                    viewModel: {
                                        data: {
                                            title: 'Редактирование типа оплаты',
                                            paymentType: response.body.data
                                        }
                                    }
                                })
                            }
                        })
                    },
                    items: [
                        {
                            iconCls: 'fas fa-edit',
                            padding: '0 5 0 2',
                            tooltip: 'Редактировать'
                        }
                    ]
                },
                {
                    xtype: 'actioncolumn',
                    width: 35,
                    handler: function (view, rowIndex, collIndex, item, e, record) {
                        ExtUtil.request({
                            url: '/web/paymentTypes/delete',
                            method: 'GET',
                            params: {
                                pid: record.get('pid')
                            },
                            success: function (response) {
                                Ext.getStore('paymentTypesStore').reload()
                            }
                        })
                    },
                    items: [
                        {
                            iconCls: 'fas fa-minus',
                            padding: '0 5 0 2',
                            tooltip: 'Удалить'
                        }
                    ]
                }
            ]
        }
    ]

})