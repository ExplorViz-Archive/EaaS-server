package net.explorviz.eaas;

import java.io.Serializable;

import org.springframework.stereotype.Service;

@Service
public class MessageBean implements Serializable {
    public String getCreatedMessage(String projectName) {
        return "Created project " + projectName;
    }

    public String getEmptyNameMessage() {
        return "Project name may not be empty!";
    }
}
