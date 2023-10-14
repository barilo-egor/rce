Ext.define('Main.view.paymentTypes.CreatePaymentTypeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.createPaymentTypeController',

    save: function (me) {
        let win = me.up('window')
        win.setLoading('Сохранение типа оплаты...')
        let form = ExtUtil.idQuery('paymentTypeCreateForm')
        if (!form.isValid()) {
            ExtMessages.incorrectlyForm()
            return
        }
        let jsonData = form.getValues()
        let requisites = []
        for (let record of Ext.getStore('createPaymentTypeRequisitesStore').getRange()) {
            requisites.push(record.getData())
        }
        jsonData.requisites = requisites
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