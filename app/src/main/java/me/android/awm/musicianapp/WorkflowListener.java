package me.android.awm.musicianapp;

import android.os.Bundle;

public interface WorkflowListener {
    String WORKFLOW_PARAM_NAME_PHASE = "WORKFLOW_PARAM_NAME_PHASE";

    enum WORKFLOW_ENUM{
        WORKFLOW_REG_WEL(0),
        WORKFLOW_REG_NAME(1),
        WORKFLOW_REG_MAIL(2),
        WORKFLOW_REG_GENERAL(3),
        WORKFLOW_REG_BIO(4),
        WORKFLOW_REG_PHOTO(5),
        WORKFLOW_REG_SKILLS(6),
        WORKFLOW_REG_DONE(7);


        private int value;
        WORKFLOW_ENUM(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        public static WORKFLOW_ENUM fromValue(int value) {
            for (WORKFLOW_ENUM workflowType : WORKFLOW_ENUM.values()) {
                if (workflowType.getValue() == value) {
                    return workflowType;
                }
            }
            return null;
        }
    }

    void manageWorkflow(WORKFLOW_ENUM workflow);

    void manageWorkflow(WORKFLOW_ENUM workflow, Bundle params);
}
