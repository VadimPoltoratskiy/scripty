import {Component, OnInit} from '@angular/core';
import {ProjectsService} from './projects.service';
import {Project} from './models/Project';

@Component({
    selector: 'app-project',
    templateUrl: './projects-list.component.html',
    styleUrls: ['./projects-list.component.scss']
})
export class ProjectsListComponent implements OnInit {
    projects: Array<Project>;
    activeProjects: Array<Project>;
    searchStr = '';
    onlyMyProjects = true;

    constructor(private projectsService: ProjectsService) {
    }

    ngOnInit() {
        localStorage.setItem('userId', '1');
        this.projectsService.getProjects()
            .subscribe((projects: Array<Project>) => {
                this.projects = projects;
                this.activeProjects = this.projects;
            });
    }

    onSearch(e: string) {
        this.searchStr = e;
    }

    showMyProjects(e: boolean) {
        this.onlyMyProjects = e;
    }

    showMore() {
        this.projectsService.getMoreProjects()
            .subscribe((projects: Array<Project>) => {
                this.projects = this.projects.concat(projects);
                this.activeProjects = this.projects;
            });
    }
}