Ext.define('Main.view.paymentTypes.CreatePaymentTypeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.createPaymentTypeController',

    save: function (me) {
        let win = me.up('window')
        win.setLoading('Сохранение типа оплаты...')
        let form = ExtUtil.idQuery('paymentTypeForm')
        if (!form.isValid()) {
            ExtMessages.incorrectlyForm()
            win.setLoading(false)
            return
        }
        let isPaymentTypeOn = ExtUtil.idQuery('isOnCheckbox').getValue()
        let saveFn = function () {
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
        if (isPaymentTypeOn && Ext.getStore('createPaymentTypeRequisitesStore').getRange()
            .filter(rec => rec.get('isOn') === true)
            .length === 0) {
            Ext.Msg.show({
                title: 'Удаление пользователя',
                message: 'Тип оплаты включен, но нет ни одного </br>включенного реквизита,что приведет к ошибке </br>при оформлении сделки. Желаете продолжить?',
                buttons: Ext.Msg.YESNO,
                icon: Ext.Msg.QUESTION,
                fn: function (btn) {
                    if (btn === 'yes') {
                        saveFn()
                    } else {
                        win.setLoading(false)
                    }
                }
            });
        } else saveFn()

    }
})