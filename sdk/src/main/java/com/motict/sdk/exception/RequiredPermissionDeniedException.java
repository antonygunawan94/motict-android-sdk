package com.motict.sdk.exception;

import java.util.List;

public class RequiredPermissionDeniedException extends Exception {
    private final List<String> deniedPermissions;

    public RequiredPermissionDeniedException(List<String> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }


    public List<String> getDeniedPermissions() {
        return deniedPermissions;
    }

    @Override
    public String toString() {
        if (deniedPermissions == null || deniedPermissions.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < deniedPermissions.size(); i++) {

            sb.append(deniedPermissions.get(i));

            if (i != deniedPermissions.size() - 1) {
                sb.append(",");
            }

        }

        return "Motict SDK need " + sb.toString() + " permissions to works properly, please add the required permissions";
    }
}
