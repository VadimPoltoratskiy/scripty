import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material';
import {ProjectCreateDialogComponent} from '../project-create-dialog/project-create-dialog.component';
import {GitImportDialogComponent} from '../git-import-dialog/git-import-dialog.component';

@Component({
    selector: 'app-projects-search',
    templateUrl: './projects-search.component.html',
    styleUrls: ['./projects-search.component.scss']
})
export class ProjectsSearchComponent implements OnInit {
    searchStr = '';
    @Output() search = new EventEmitter<string>();
    /*onlyMyProjects = true;
    @Output() myProjectsCheck = new EventEmitter<boolean>();*/

    onChange() {
        this.search.emit(this.searchStr);
    }

    /*showMyProjects() {
        this.myProjectsCheck.emit(this.onlyMyProjects);
    }*/

    constructor(private dialog: MatDialog) {
    }

    ngOnInit(): void {
    }

    openDialog() {
        this.dialog.open(ProjectCreateDialogComponent, {});
    }

    openImportDialog() {
        this.dialog.open(GitImportDialogComponent, {});
    }
}
