
package com.clockwork.export.binary;

class BinaryIdContentPair {

    private int id;
    private BinaryOutputCapsule content;
    
    BinaryIdContentPair(int id, BinaryOutputCapsule content) {
        this.id = id;
        this.content = content;
    }

    BinaryOutputCapsule getContent() {
        return content;
    }

    void setContent(BinaryOutputCapsule content) {
        this.content = content;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
