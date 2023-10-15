Ext.define('Main.view.paymentTypes.RequisiteGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'requisitegrid',
    padding: '20 20 20 20',
    title: 'Реквизиты',
    store: {
        storeId: 'createPaymentTypeRequisitesStore',
        fields: ['pid', 'name', 'requisite', 'isOn']
    },
    listeners: {
        afterrender: function (me) {
            let paymentType = me.up('window').getViewModel().getData().paymentType
            if (!paymentType) return
            ExtUtil.request({
                    async: 'false',
                    url: '/web/paymentTypes/getRequisites',
                    params: {
                        paymentTypePid: paymentType.pid
                    },
                    method: 'GET',
                    success: function (response) {
                        me.getStore().add(response.body.data)
                    }
                }
            )
        }
    },
    dockedItems: [
        {
            xtype: 'toolbar',
            items: [
                {
                    iconCls: 'fas fa-plus',
                    tooltip: 'Добавить реквизит',
                    handler: function (me) {
                        Ext.create('Main.view.paymentTypes.requisites.CreateRequisiteWindow', {
                            viewModel: {
                                data: {}
                            }
                        })
                    }
                }
            ]
        }
    ],
    columns: [
        {
            text: 'Наименование',
            flex: 1,
            dataIndex: 'name'
        },
        {
            xtype: 'checkcolumn',
            dataIndex: 'isOn',
            text: '<i class="fas fa-power-off"></i>',
            width: 35,
            tooltip: 'Включен ли реквизит',
            listeners: {
                checkchange: function () {
                    if (Ext.getStore('createPaymentTypeRequisitesStore').getRange()
                        .filter(rec => rec.get('isOn') === true)
                        .length < 2) {
                        ExtUtil.idQuery('isDynamicOnField').setValue(false)
                    } else {
                        ExtUtil.idQuery('isDynamicOnField').setValue(true)
                    }
                }
            }
        },
        {
            xtype: 'actioncolumn',
            width: 30,
            items: [
                {
                    iconCls: 'fas fa-edit',
                    tooltip: 'Редактировать',
                    handler: function (view, rowIndex, collIndex, item, e, record) {
                        Ext.create('Main.view.paymentTypes.requisites.EditRequisiteWindow', {
                            viewModel: {
                                data: {
                                    requisite: record.getData(),
                                    rowIndex: rowIndex
                                }
                            }
                        })
                    }
                }
            ]
        },
        {
            xtype: 'actioncolumn',
            width: 30,
            items: [
                {
                    iconCls: 'fas fa-minus redColor',
                    padding: '0 5 0 2',
                    tooltip: 'Удалить'
                }
            ],
            handler: function (view, rowIndex, collIndex, item, e, record) {
                Ext.getStore('createPaymentTypeRequisitesStore').remove(record)
            }
        }
    ]
})