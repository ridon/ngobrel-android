package id.ridon.ngobrel.contoh.model;

public class SelectableContact extends Person {
    private boolean isSelected = false;

    public SelectableContact(Person person, boolean isSelected){
        super(person.getId(), person.getName(), person.getEmail(),person.getJob());
        this.setAvatarUrl(person.getAvatarUrl());
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
