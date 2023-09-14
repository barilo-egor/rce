Ext.define('Main.view.api.registration.ApiRegistrationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.apiRegistrationController',

    register: function (btn) {
        let form = ExtUtil.idQuery('apiuserregisteform')
        if (!form.isValid()) {
            Ext.Msg.alert('Внимание', 'Неверно заполнена форма.')
            return
        }
        let selection = ExtUtil.idQuery('requisitesTree').getSelection()
        if (!selection || selection.length === 0) {
            Ext.Msg.alert('Внимание', 'Выберите реквизит для покупки')
            return
        }
        let requisitePid = selection[0].getData().pid
        if (!requisitePid) {
            Ext.Msg.alert('Внимание', 'Выберите реквизит для покупки')
            return
        }
        let jsonData = form.getValues()
        jsonData.buyRequisitePid = requisitePid
        form.setLoading('Загрузка')
        Ext.Function.defer(function() {
            Ext.Ajax.request({
                url: 'api/user/save',
                method: 'POST',
                jsonData: jsonData,
                success: function (rs) {
                    let response = Ext.JSON.decode(rs.responseText)
                    Ext.Msg.alert('Сохранено',
                        '<div style="text-align: center;">Токен пользователя: <p/><b>' + response.token + '</b></div>')
                    form.reset()
                    form.setLoading(false)
                }
            })
        }, 10);
    }
})