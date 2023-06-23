let RegexUtil = {
    onlyLettersAndNumbers: function (str) {
        return /^[A-Za-z0-9]*$/.test(str);
    }
}