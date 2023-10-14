Ext.define('Main.view.paymentTypes.requisites.CreateRequisiteWindow', {
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
                let range = store.getRange()
                if (range.length > 0 && store.getRange().filter(record => record.get('name') === values.name).length > 0) {
                    Ext.Msg.alert('Внимание', 'Реквизит с таким наименованием уже добавлен.')
                    return
                }
                values.isOn = false
                store.add(values)
                ExtUtil.idQuery('requisiteForm').reset()
                me.up('window').close()
                Ext.toast('Реквизит добавлен.')
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