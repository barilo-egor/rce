let ValidatorUtil = {
    validatePositiveInt: function (val) {
        if (!val) return 'Введите значение.'
        if (val === '0' || val === 0 || val < 1) return 'Введите значение больше 0.'
        else return true
    },

    validateNotEmpty: function (val) {
        if (!val) return 'Введите значение'
        return true
    },

    validateNotEmptyAndLettersAndNumber: function (val) {
        if (!val) return 'Введите значение'
        if (RegexUtil.onlyLettersAndNumbers(val)) return true;
        else return 'Только латинские буквы и цифры.'
    },

    validateDiscount: function (val) {
        if (!val) return 'Введите значение.'
        if (val < -99 || val > 99) {
            return 'Значение должно быть от -99 до 99.'
        } else return true
    },

    validateId: function (val) {
        if (!val || val === '') return  'Введите значение'
        if (!RegexUtil.onlyLettersAndNumbers(val)) return 'Только латинские буквы и символы'
        let result
        Ext.Ajax.request({
            url: 'api/user/isExistById',
            params: {
                id: val
            },
            method: 'GET',
            async: false,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                if (response.result) result = 'Данный идентификатор уже занят'
                else result = true
            }
        })
        return result
    },

    validateLogin: function (val) {
        if (!val) return 'Введите значение'
        if (!RegexUtil.onlyLettersAndNumbers(val)) return 'Только латинские буквы и цифры.'
        let result = true
        Ext.Ajax.request({
            url: '/web/registration/isUsernameFree',
            method: 'GET',
            params: {
                username: val
            },
            async: false,
            success: function(rs) {
                let response = Ext.JSON.decode(rs.responseText)
                if (!response.result) result = 'Данный логин уже занят'
            },
            failure: function () {
                Ext.Msg.alert('Ошибка', 'Ошибка при попытке проверки логина.')
            }
        })
        return result
    },

    validatePasswordConfirm: function (val) {
        let passwordInput = ExtUtil.idQuery('passwordInput')
        if (!val) return 'Введите значение'
        if (passwordInput.value !== val) return 'Пароли не совпадают'
        return true
    },
}