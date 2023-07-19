Ext.define('Main.view.bulkDiscount.BulkDiscountGridPanel', {
    extend: 'Ext.grid.Panel',
    xtype: 'bulkdiscountgridpanel',
    controller: 'bulkDiscountController',
    scrollable: true,
    columns: [{
        header: 'Сумма',
        dataIndex: 'value',
        flex: 0.65,
        editor: {
            xtype: 'numberfield',
            selectOnFocus: false,
            allowBlank: false,
        },
    }, {
        header: 'Скидка',
        dataIndex: 'percent',
        flex: 0.35,
        editor: {
            xtype: 'numberfield',
            selectOnFocus: false,
            allowBlank: false,
        }
    }
    ],
    tbar: [{
        text: 'Добавить',
        handler: 'onAddClick'
    }],
    fbar: [{
        text: 'Сохранить',
        handler: 'onSaveClick'
    }],
    plugins: {
        cellediting: {
            clicksToEdit: 1
        }
    }
});