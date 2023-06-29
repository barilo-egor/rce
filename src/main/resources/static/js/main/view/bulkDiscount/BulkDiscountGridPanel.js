Ext.define('Main.view.bulkDiscount.BulkDiscountGridPanel', {
    extend: 'Ext.grid.Panel',
    xtype: 'bulkdiscountgridpanel',
    controller: 'bulkDiscountController',
    scrollable: true,
    columns: [{
        header: 'Сумма',
        dataIndex: 'value',
        flex: 0.5,
    }, {
        header: 'Скидка',
        dataIndex: 'percent',
        flex: 0.5,
    }
    ]
})