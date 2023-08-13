Ext.define('Main.view.bulkDiscount.BulkDiscountGridPanel', {
    extend: 'Ext.grid.Panel',
    xtype: 'bulkdiscountgridpanel',
    controller: 'bulkDiscountController',
    scrollable: true,
    enableColumnHide: false,
    columns: [{
        header: 'Сумма',
        dataIndex: 'sum',
        flex: 0.65,
    }, {
        header: 'Скидка',
        dataIndex: 'percent',
        flex: 0.35,
    }, {
        xtype: 'actioncolumn',
        width: 30,
        sortable: false,
        menuDisabled: true,
        items: [
            {
                iconCls: 'fas fa-pen',
                tooltip: 'Редактировать',
                handler: 'onEditClick'
            }
        ]
    }, {
        xtype: 'actioncolumn',
        width: 30,
        sortable: false,
        menuDisabled: true,
        items: [
            {
                iconCls: 'fas fa-minus',
                tooltip: 'Удалить',
                handler: 'onRemoveClick'
            }
        ]
    }
    ],
    tbar: [{
        iconCls: 'fas fa-plus',
        tooltip: 'Добавить',
        handler: 'onAddClick'
    }]
});