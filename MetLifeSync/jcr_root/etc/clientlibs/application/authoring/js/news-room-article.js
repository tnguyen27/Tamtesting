Metlife.Components.NewsRoomArticle = {

    NameSpacesOptions : function(path , record) {

        var tagUrl = "/etc/tags/metlife.1.json",
         url = CQ.HTTP.noCaching(tagUrl),
            arrayElements = [];
        var tags = CQ.HTTP.eval(url);
        console.log(tags);

        for (var tag in tags) {
            if (tags.hasOwnProperty(tag)) {
               if(typeof tags[tag]  == "object"){
                    arrayElements.push({
                        text: tags[tag]["jcr:title"],
                        value: tag
                    })
               }

            }
        }

        return arrayElements;
    }

};

Metlife.Components.NewsRoomArticle.TopicTags = {
    beforeLoadContent : function(field,record,path ){
        $.get(path+'.namespaces.json', function(data){
            if(data) {
                field.tagsBasePath = '/etc/tags/metlife/' + data[0];
            }
            var v = record.get(field.getName()),
                newTags = v.map(function(entry) {
                    var noNamespace = entry.replace("metlife:","");
                    return noNamespace = noNamespace.replace("/",":");
                        //return entry.replace("metlife:"+data[0]+"/",data[0]+':');

                });
            v = newTags;
            field.setValue = Metlife.Components.NewsRoomArticle.TopicTags.setValueNew;
            if (v == undefined && field.defaultValue != null) {
                if (field.isApplyDefault(record, path)) {
                    field.setValue(field.defaultValue);
                }
            }
            else {
                field.setValue(v);
            }

            field.fireEvent('loadcontent', field, record, path);
        });

        return false;
    },
    setValueNew:  function(valueArray, partialTags) {
        this.clear();
        this.initLocale();

        if (valueArray) {
            var tags = null;

            // optimization: in a cq Dialog, we know the content path, and if this
            // is a standard cq:tags property, we can load all tag definitions for
            // the tags referenced by that resource in one go
            if (this.contentPath && (this.name === "cq:tags" || this.name === "./cq:tags") ) {
                // load all tags referenced by this resource from server (faster than many multiple requests per tag below)
                // 2nd argument = cache killer needed for IE7/8 set to automated caching
                var tagJson = this.loadJson(this.contentPath + CQ.tagging.TAG_LIST_JSON_SUFFIX + "?count=false", true);
                if (tagJson && tagJson.tags) {
                    tags = tagJson.tags;
                }

                // reset so that the next, possibly manual setValue() call works based on the passed array only
                this.contentPath = null;
            }

            if (!tags || tags.length === 0) {
                tags = [];

                // go through values (which are tag ID strings) and load their tag definitions
                for (var i=0, iEnd = valueArray.length; i < iEnd; i++) {
                    var tagID = valueArray[i];
                    var tagInfo = CQ.tagging.parseTagID(tagID);

                    // load single tag data from server
                    if (tagInfo.namespace !==  "metlife"){
                    var tag = this.loadJson(this.tagsBasePath.slice(0,this.tagsBasePath.lastIndexOf('/'))+'/'+ tagInfo.namespace + "/" + tagInfo.local + CQ.tagging.TAG_JSON_SUFFIX);
                    }else {
                        var tag = this.loadJson(this.tagsBasePath.slice(0,this.tagsBasePath.lastIndexOf('/'))+'/' + tagInfo.local + CQ.tagging.TAG_JSON_SUFFIX);

                    }
                    tags.push(tag || tagID);
                }

                // handle partial values
                if (partialTags) {
                    for (var i=0, iEnd = partialTags.length; i < iEnd; i++) {
                        var tagID = partialTags[i];
                        var tagInfo = CQ.tagging.parseTagID(tagID);

                        // load single tag data from server
                        var tag = this.loadJson(this.tagsBasePath + "/" + tagInfo.namespace + "/" + tagInfo.local + CQ.tagging.TAG_JSON_SUFFIX);
                        if (tag) {
                            // only look at existing tags for parital tags
                            tag.partial = true;
                            tags.push(tag);
                        }
                    }
                }
            }

            // internally add all tags
            CQ.Ext.each(tags, function(tag) {
                var namespace = CQ.tagging.parseTagID(tag.tagID || tag).namespace;

                if (typeof tag === "string" || !this.isAllowedNamespace(namespace)) {
                    // not allowed namespace, keep pure tagID in the background
                    this.hiddenTagIDs.push(tag.tagID || tag);
                } else {
                    // allowed => display
                    if (tag.partial) {
                        delete tag.partial;
                        this.internalAddTag(tag, "partial");
                    } else {
                        this.internalAddTag(tag, "set");
                    }
                }
            }, this);
        }

        // rebuild this.value
        this.getValue();

        this.inputDummy.doLayout();
    }

};
