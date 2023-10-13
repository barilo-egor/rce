Ext.define('Main.view.paymentTypes.CreatePaymentTypeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.createPaymentTypeController',

    save: function (me) {
        let win = me.up('window')
        win.setLoading('Сохранение типа оплаты...')
        let form = ExtUtil.idQuery('paymentTypeCreateForm')
        if (!form.isValid()) {
            Ext.Msg.alert('Внимание', 'Неверно заполнена форма')
            return
        }
        let jsonData = form.getValues()
        ExtUtil.request({
            url: '/web/paymentTypes/save',
            jsonData: jsonData,
            success: function (response) {
                Ext.getStore('paymentTypesStore').reload()
                win.setLoading(false)
                Ext.Msg.alert('Информация', 'Тип оплаты <b>' + response.body.data.name + '</b> успешно сохранен.')
                ExtUtil.closeWindow(me)
            },
            loadingComponent: win
        })
    }
})