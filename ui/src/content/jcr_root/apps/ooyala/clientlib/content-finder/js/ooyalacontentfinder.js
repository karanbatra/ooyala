/**
 * Copyright (c) 2012, Ooyala, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * •    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * •    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

if (typeof Ooyala == 'undefined') {
    Ooyala = {};
}

Ooyala.ContentFinderRadioGroup = function(config) {
    config = {
        "xtype": "radiogroup",
        "allowBlank": false,
        "name": "searchBy",
        "columns": 1,
        "id": "radioGroupOoyala",
        "value": "label",
        "items": [{
            "xtype": "radio",
            "autoWidth": true,
            "fieldLabel": CQ.I18n.getMessage("Label"),
            "name": "searchBy",
            "id": "searchByLabel",
            "value": "label",
            "inputValue": "label",
            "handler": function(checkbox, checked) {
                this.findParentByType('ooyalacontentfindertab').suggest = this.getGroupValue();
            }
        },{
            "xtype": "radio",
            "autoWidth": true,
            "fieldLabel": CQ.I18n.getMessage("Title"),
            "name": "searchBy",
            "inputValue": "title",
            "id": "searchByTitle",
            "value": "title"
        },{
            "xtype": "radio",
            "autoWidth": true,
            "fieldLabel": CQ.I18n.getMessage("Description"),
            "name": "searchBy",
            "inputValue": "description",
            "id": "searchByDescription"
        },{
            "xtype": "radio",
            "autoWidth": true,
            "fieldLabel": CQ.I18n.getMessage("Metadata"),
            "name": "searchBy",
            "inputValue": "meta",
            "id": "searchByMeta"
        }
    ]};
    return config;
};

Ooyala.ContentFinderTab = CQ.Ext.extend(CQ.wcm.ContentFinderTab, {
    getParams: function(comp) {
        var fields;
        if ((this.fields == null) && comp) {
            var form = comp.findParentByType("form");
            fields = CQ.Util.findFormFields(form);
        } else {
            fields = this.fields;
        }

        var params = new Object();
        for (var name in this.fields) {
            var f = this.fields[name];
            for (var i = 0; i < f.length; i++) {
                params[name] = f[i].getValue();
                if (f[i].getValue() instanceof Object) {
                    params[name] = f[i].getValue().getId();
                }
            }
        }
        return params;
    }
});
CQ.Ext.reg("ooyalacontentfindertab", Ooyala.ContentFinderTab);

Ooyala.SuggestField = CQ.Ext.extend(CQ.form.SuggestField, {
    keyupListener: function(comp, evt) {
        if (!this.findParentByType('ooyalacontentfindertab') || !this.findParentByType('ooyalacontentfindertab').suggest) {
            return;
        }
        var suggest = this.findParentByType('ooyalacontentfindertab').suggest;
        if (suggest != 'label' && suggest != 'meta') {
            return;
        }

        this.store.baseParams.type = this.findParentByType('ooyalacontentfindertab').suggest;
        var currentValue = this.getRawValue();
        var caretPos = this.getCaretPosition();
        var currentTerm = this.getTermAt(caretPos);

        var key = evt.getKey();

        if (evt.isSpecialKey()) {
            if (key == 13) {
                // [enter]
                this.fireEvent("search", this, currentValue);
                if (this.isExpanded && this.selectedIndex == -1) {
                    this.collapse();
                }
                return;
            }

            if (!this.isExpanded()) {
                if (key == 40) {
                    // [down arrow]: force suggestions
                    this.suggest(currentTerm, currentValue, caretPos);
                    return;
                }
            }

            return;
        }

        if (currentTerm.length < this.minTermLength) {
            return;
        }

        if (key == 8 && /\/$/.test(currentTerm) == false) {
            // [backspace] and last char not a slash
            return;
        }

        var sf = this;

        // suggest
        window.clearTimeout(this.suggestIntervalId);
        this.suggestIntervalId = window.setTimeout(function() {
            sf.suggest(currentTerm, currentValue, caretPos);
        }, this.suggestDelay);

        // search
        if (this.searchDelay) {
            window.clearTimeout(this.searchIntervalId);
            this.searchIntervalId = window.setTimeout(function() {
                sf.fireEvent("search", sf, currentValue);
            }, this.searchDelay);
        }
    }
});
CQ.Ext.reg("ooyalasuggestfield", Ooyala.SuggestField);
