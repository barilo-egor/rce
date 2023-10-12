Ext.define('Main.view.paymentTypes.PaymentTypesPanel', {
    extend: 'Main.view.components.FramePanel',
    xtype: 'paymenttypespanel',
    title: {
        xtype: 'mainframetitle',
        text: 'Типы оплат'
    },

    padding: '0 0 0 20',
    items: [
        {
            xtype: 'grid',
            emptyText: 'Нет записей',
            store: {
                storeId: 'paymentTypesStore',
                fields: ['name', 'isOn'],
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
                            return '<i class="fas fa-circle redColor"></i>'
                        } else {
                            return '<i class="fas fa-circle limeColor"></i>'
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    width: 35,
                    items: [
                        {
                            iconCls: 'fas fa-edit',
                            padding: '0 5 0 2'
                        }
                    ]
                }
            ]
        }
    ]

})