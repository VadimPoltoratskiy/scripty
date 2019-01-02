import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Project} from '../../../../models/Project';
import {ProjectEditDialogComponent} from '../project-edit-dialog/project-edit-dialog.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {ProjectsService} from '../projects.service';
import {ToastService} from '../../../../services/toast.service';

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
    @Input() projects: Project[];
    @Input() searchStr: string;
    confirmDialogRef: MatDialogRef<ConfirmDialogComponent>;
    editDialogRef: MatDialogRef<ProjectEditDialogComponent>;
    @Output() changeList = new EventEmitter();
    displayedColumns: string[] = ['name', 'description', 'author', 'edit'];
    userName: string;

    constructor(private dialog: MatDialog,
                private projectsService: ProjectsService,
                private toastService: ToastService) {
    }

    ngOnInit() {
        this.userName = localStorage.getItem('user');
        // this.userName = 'user@test.co';
    }

    openEditDialog(project) {
        this.editDialogRef = this.dialog.open(ProjectEditDialogComponent, {
            data: project
        });
        this.editDialogRef.afterClosed()
            .subscribe(
                result => {
                    if (result) {
                        this.changeList.emit();
                        this.toastService.showSuccess(`Project '${project.name}' was updated`);
                    }
                }
            );
    }

    openArchiveDialog(project: Project, value: boolean) {
        this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {});
        this.confirmDialogRef.componentInstance.confirmMessage = value ?
            `Do you want to archive '${project.name}'?` :
            `Do you want to unarchive '${project.name}'?`;
        this.confirmDialogRef.afterClosed()
            .subscribe(result => {
                if (result) {
                    this.projectsService.archiveProject(project.id, value)
                        .subscribe(
                            () => {
                                this.changeList.emit();
                                this.toastService.showSuccess(`Archive status of '${project.name}' was changed`);
                            },
                            (error) => {
                                console.log(error);
                            }
                        );
                }
                this.confirmDialogRef = null;
            });
    }

    openDeleteDialog(name: string) {
        this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {});
        this.confirmDialogRef.componentInstance.confirmMessage = `Do you want to delete '${name}'?`;
        this.confirmDialogRef.afterClosed()
            .subscribe(result => {
                if (result) {
                    // delete project
                }
                this.confirmDialogRef = null;
            });
    }
}
