package com.jaredsburrows.license.internal.report

import com.jaredsburrows.license.internal.pom.Developer
import com.jaredsburrows.license.internal.pom.License
import com.jaredsburrows.license.internal.pom.Project
import spock.lang.Specification

import static test.TestUtils.assertCsv

final class CsvReportSpec extends Specification {
  def 'no open source'() {
    given:
    def projects = []
    def sut = new CsvReport(projects)

    when:
    def actual = sut.toString()
    def expected = ""

    then:
    assertCsv(expected, actual)
  }

  def 'open source - missing values'() {
    given:
    def developer = new Developer(name: 'developer-name')
    def project1 = new Project(
      name: 'project-name',
      developers: [],
      gav: 'foo:bar:1.2.3'
    )
    def project2 = new Project(
      name: 'project-name',
      developers: [developer, developer],
      gav: 'foo:bar:1.2.3'
    )
    def projects = [project1, project2]
    def sut = new CsvReport(projects)

    when:
    def actual = sut.toString().stripIndent().trim()
    def expected =
      """
      project,description,version,developers,url,year,licenses,license urls,dependency
      project-name,null,null,null,null,null,null,null,foo:bar:1.2.3
      project-name,null,null,\"developer-name,developer-name\",null,null,null,null,foo:bar:1.2.3
      """.stripIndent().trim()

    then:
    assertCsv(expected, actual)
  }

  def 'open source - all values'() {
    given:
    def developer = new Developer(name: 'developer-name')
    def developers = [developer, developer]
    def license = new License(
      name: 'license-name',
      url: 'license-url'
    )
    def project = new Project(
      name: 'project-name',
      description: 'project-description',
      version: '1.0.0',
      licenses: [license],
      url: 'project-url',
      developers: developers,
      year: 'project-year',
      gav: 'foo:bar:1.2.3'
    )
    def projects = [project, project]
    def sut = new CsvReport(projects)

    when:
    def actual = sut.toString().stripIndent().trim()
    def expected =
      """
      project,description,version,developers,url,year,licenses,license urls,dependency
      project-name,project-description,1.0.0,\"developer-name,developer-name\",project-url,project-year,license-name,license-url,foo:bar:1.2.3
      project-name,project-description,1.0.0,\"developer-name,developer-name\",project-url,project-year,license-name,license-url,foo:bar:1.2.3
      """.stripIndent().trim()

    then:
    assertCsv(expected, actual)
  }
}
