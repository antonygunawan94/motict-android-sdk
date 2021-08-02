package com.motict.sdk.exception;

import java.util.ArrayList;
import java.util.List;

public class PermissionDeniedException extends Exception {
    private final List<String> deniedRequiredPermissions;
    private final List<String> deniedOptionalPermissions;

    public PermissionDeniedException(List<String> deniedRequiredPermissions, List<String> deniedOptionalPermissions) {
        this.deniedRequiredPermissions = deniedRequiredPermissions;
        this.deniedOptionalPermissions = deniedOptionalPermissions;
    }


    public List<String> getDeniedRequiredPermissions() {
        return deniedRequiredPermissions;
    }

    public List<String> getDeniedOptionalPermissions() {
        return deniedOptionalPermissions;
    }

    public List<String> getAllDeniedPermissions() {
        final List<String> allDeniedPermissions = new ArrayList<>(deniedRequiredPermissions);
        allDeniedPermissions.addAll(deniedOptionalPermissions);
        return allDeniedPermissions;
    }

    @Override
    public String toString() {
        if (deniedRequiredPermissions == null || deniedRequiredPermissions.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < deniedRequiredPermissions.size(); i++) {

            sb.append(deniedRequiredPermissions.get(i));

            if (i != deniedRequiredPermissions.size() - 1) {
                sb.append(",");
            }

        }

        return "Motict SDK need " + sb.toString() + " permissions to works properly, please add the required permissions";
    }
}
