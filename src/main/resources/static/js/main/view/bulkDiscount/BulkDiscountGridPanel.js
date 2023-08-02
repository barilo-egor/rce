Ext.define('Main.view.bulkDiscount.BulkDiscountGridPanel', {
    extend: 'Ext.grid.Panel',
    xtype: 'bulkdiscountgridpanel',
    controller: 'bulkDiscountController',
    scrollable: true,
    columns: [{
        header: 'Сумма',
        dataIndex: 'sum',
        flex: 0.65,
        // editor: {
        //     xtype: 'numberfield',
        //     selectOnFocus: false,
        //     allowBlank: false,
        // },
    }, {
        header: 'Скидка',
        dataIndex: 'percent',
        flex: 0.35,
        editor: {
            xtype: 'numberfield',
            selectOnFocus: false,
            allowBlank: false,
        }
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
    }],
    // fbar: [{
    //     text: 'Сохранить',
    //     iconCls: 'fas fa-save greenBtn',
    //     cls: 'greenBtn',
    //     handler: 'onSaveClick'
    // }],
    plugins: {
        cellediting: {
            clicksToEdit: 1
        }
    }
});