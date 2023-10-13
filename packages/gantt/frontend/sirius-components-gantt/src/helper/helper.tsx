import { Task } from '@SemanticBoard/gantt-task-react';
import { TaskType } from '@SemanticBoard/gantt-task-react/dist/types/public-types';
import { GQLTask, GQLTaskType, SelectableTask } from '../representation/GanttRepresentation.types';
export function getTaskFromGQLTask(gQLTasks: GQLTask[], parentId: string) {
  const tasks: Task[] = [];
  gQLTasks.forEach((gQLTask) => {
    let type: TaskType = 'task';
    if (gQLTask.detail.type === GQLTaskType.TASK_GROUP) {
      type = 'project';
    } else if (gQLTask.detail.type === GQLTaskType.MILESTONE) {
      type = 'milestone';
    }
    const task: SelectableTask = {
      id: gQLTask.id,
      name: gQLTask.detail.name,
      start: new Date(Date.parse(gQLTask.detail.startDate)),
      end: new Date(Date.parse(gQLTask.detail.endDate)),
      progress: gQLTask.detail.progress,
      type,
      dependencies: gQLTask.dependencies?.map((dep) => dep.id),
      project: parentId,
      hideChildren: false,
      targetObjectId: gQLTask.targetObjectId,
      targetObjectKind: gQLTask.targetObjectKind,
      targetObjectLabel: gQLTask.targetObjectLabel,
    };

    tasks.push(task);
    if (gQLTask.detail.type === GQLTaskType.TASK_GROUP) {
      const children: Task[] = getTaskFromGQLTask(gQLTask.subTasks, gQLTask.id);
      tasks.push(...children);
    }
  });
  return tasks;
}
