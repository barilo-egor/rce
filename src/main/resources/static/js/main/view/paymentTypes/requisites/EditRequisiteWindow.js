Ext.define('Main.view.paymentTypes.requisites.EditRequisiteWindow', {
    extend: 'Ext.window.Window',
    width: '85%',
    height: '23%',
    modal: true,
    autoShow: true,
    title: 'Создание реквизита',
    layout: 'fit',
    buttonAlign: 'center',
    requires: ['Main.view.paymentTypes.requisites.RequisiteForm'],
    buttons: [
        {
            xtype: 'savebutton',
            handler: function (me) {
                let values = ExtUtil.idQuery('requisiteForm').getValues()
                let store = Ext.getStore('createPaymentTypeRequisitesStore')
                let record = store.getAt(me.up('window').getViewModel().getData().rowIndex)
                let range = store.getRange()
                if (range.length > 0 && store.getRange().filter(record => record.get('name') === values.name).length > 0) {
                    Ext.Msg.alert('Внимание', 'Реквизит с таким наименованием уже добавлен.')
                    return
                }
                record.set('name', values.name)
                record.set('requisite', values.requisite)
                ExtUtil.idQuery('requisiteForm').reset()
                me.up('window').close()
                Ext.toast('Реквизит обновлен.')
            }
        },
        {
            xtype: 'cancelbutton',
            handler: ExtUtil.closeWindow
        }
    ],
    items: [
        {
            xtype: 'requisiteform'
        }
    ]
})