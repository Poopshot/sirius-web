import { Task } from '@SemanticBoard/gantt-task-react';
import { TaskType } from '@SemanticBoard/gantt-task-react/dist/types/public-types';
import { GQLTask, SelectableTask } from '../representation/GanttRepresentation.types';
export function getTaskFromGQLTask(gQLTasks: GQLTask[], parentId: string) {
  const tasks: Task[] = [];
  gQLTasks.forEach((gQLTask) => {
    let type: TaskType = 'task';
    const isProject = gQLTask.subTasks && gQLTask.subTasks.length > 0;
    if (isProject) {
      type = 'project';
    } else if (gQLTask.detail.startDate === gQLTask.detail.endDate) {
      type = 'milestone';
    }
    const task: SelectableTask = {
      id: gQLTask.id,
      name: gQLTask.detail.name,
      start: new Date(gQLTask.detail.startDate * 1000),
      end: new Date(gQLTask.detail.endDate * 1000),
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
    if (isProject) {
      const children: Task[] = getTaskFromGQLTask(gQLTask.subTasks, gQLTask.id);
      tasks.push(...children);
    }
  });
  return tasks;
}
