package project.prototype;

class StateTaskKey {
    private Integer state;
    private Integer task;

    public StateTaskKey(Integer state, Integer task)
    {
        this.state = state;
        this.task = task;
    }

    public Integer getState() {
        return state;
    }

    public Integer getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StateTaskKey) {
            return state.equals(((StateTaskKey)o).state) &&
                task.equals(((StateTaskKey)o).task);
        }
        return false;
    }

    @Override
	public int hashCode() {
		return state.hashCode() + 31 * task.hashCode();
	}

    @Override
    public String toString() {
        return "(" + state + ";" + task + ")";
    }
}
